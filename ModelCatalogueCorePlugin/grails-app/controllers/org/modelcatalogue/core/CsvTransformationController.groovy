package org.modelcatalogue.core

import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.springframework.web.multipart.MultipartFile

class CsvTransformationController extends AbstractRestfulController<CsvTransformation>{

    def dataArchitectService

    static allowedMethods = [transform: "POST"]

    CsvTransformationController() {
        super(CsvTransformation)
    }

    def transform() {
        if (!params.id) {
            notFound()
        }

        if (!modelCatalogueSecurityService.hasRole('USER')) {
            notAuthorized()
            return
        }

        CsvTransformation transformation = CsvTransformation.get(params.id)

        if (!transformation) {
            notFound()
        }

        MultipartFile file = request.getFile('csv')

        params.separator = params.separator ?: ';'

        response.setHeader("Content-Disposition", "filename=${transformation.name}.csv")
        response.setHeader("Content-Type", "text/csv")
        file.inputStream.withReader {
            dataArchitectService.transformData(params, transformation, it, response.getWriter())
        }
    }

    @Override
    protected bindRelations(CsvTransformation instance, Object objectToBind) {
        if (objectToBind.columns != null) {
            for (definition in objectToBind.columns) {
                ColumnTransformationDefinition columnTransformationDefinition = new ColumnTransformationDefinition(
                        transformation: instance,
                        source: getByIdOrNull(definition.source),
                        destination: getByIdOrNull(definition.destination),
                        header: definition.header
                )
                columnTransformationDefinition.save(failOnError: true)
            }
        }
    }

    private static DataElement getByIdOrNull(sourceOrDestination) {
        if (!sourceOrDestination) return null
        if (sourceOrDestination instanceof JSONObject.Null) return null
        DataElement.get(sourceOrDestination.id)
    }

    // column definitions deleted on cascade
    protected checkAssociationsBeforeDelete(CsvTransformation instance) { }

    @Override
    protected cleanRelations(CsvTransformation instance) {
        if (instance.columnDefinitions) {
            def definitions = new ArrayList<ColumnTransformationDefinition>(instance.columnDefinitions)
            for (columnDefinition in definitions) {
                columnDefinition.transformation = null
                instance.removeFromColumnDefinitions columnDefinition
                if (columnDefinition.id) {
                    columnDefinition.delete(flush: true)
                }
            }
            instance.columnDefinitions.clear()
        }
    }
}
