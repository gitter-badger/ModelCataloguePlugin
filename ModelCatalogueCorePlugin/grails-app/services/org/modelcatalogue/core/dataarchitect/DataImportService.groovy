package org.modelcatalogue.core.dataarchitect

import grails.transaction.Transactional
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.ValueDomain


class DataImportService {

    static transactional = true

    private static final QUOTED_CHARS = ["\\": "&#92;", ":" : "&#58;", "|" : "&#124;", "%" : "&#37;"]
    //the import script accepts and array of headers these should include the following:
    //Data Item Name, Data Item Description, Parent Section, Section, Measurement Unit, Data type
    //these will allow the import script to identify the rows

    def importData(ArrayList headers, ArrayList rows, String conceptualDomain, String conceptualDomainDescription, ArrayList parentModels) {
        //get indexes of the appropriate sections

        def newImporter = new Importer(parentModels:parentModels)

        def dataItemNameIndex = headers.indexOf("Data Item Name")
        def dataItemCodeIndex = headers.indexOf("Data Item Unique Code")
        def dataItemDescriptionIndex = headers.indexOf("Data Item Description")
        def parentModelIndex = headers.indexOf("Parent Model")
        def modelIndex = headers.indexOf("Model")
        def unitsIndex = headers.indexOf("Measurement Unit")
        def dataTypeIndex = headers.indexOf("Data type")
        def metadataStartIndex = headers.indexOf("Metadata") + 1
        def metadataEndIndex = headers.size() - 1
        def parentModelCodeIndex = headers.indexOf("Parent Model Unique Code")
        def modelCodeIndex = headers.indexOf("Model Unique Code")
        def elements = []
        if (dataItemNameIndex == -1) throw new Exception("Can not find 'Data Item Name' column")
        //iterate through the rows and import each line
        rows.eachWithIndex { def row, int i ->

            ImportRow importRow = new ImportRow()

            importRow.dataElementName = (dataItemNameIndex!=-1)?row[dataItemNameIndex]:null
            importRow.dataElementCode = (dataItemCodeIndex!=-1)?row[dataItemCodeIndex]:null
            importRow.parentModelName = (parentModelIndex!=-1)?row[parentModelIndex]:null
            importRow.parentModelCode = (parentModelCodeIndex!=-1)?row[parentModelCodeIndex]:null
            importRow.containingModelName = (modelIndex!=-1)?row[modelIndex]:null
            importRow.containingModelCode = (modelCodeIndex!=-1)?row[modelCodeIndex]:null
            importRow.dataType =   (dataTypeIndex!=-1)?row[dataTypeIndex]:null
            importRow.dataElementDescription =   (dataItemDescriptionIndex!=-1)?row[dataItemDescriptionIndex]:null
            importRow.measurementUnitName =   (unitsIndex!=-1)?row[unitsIndex]:null
            importRow.conceptualDomainName = conceptualDomain
            importRow.conceptualDomainDescription = conceptualDomainDescription
            importRow.parentModelCode = (parentModelCodeIndex!=-1)?row[parentModelCodeIndex]:null

            def counter = metadataStartIndex
            def metadataColumns = [:]
            while (counter <= metadataEndIndex) {
                metadataColumns.put(headers[counter], row[counter])
                counter++
            }
            importRow.metadata = (metadataColumns)?metadataColumns:null

            newImporter.ingestRow(importRow)
            //importLine(conceptualDomain, conceptualDomainDescription, parentModels, name, valueDomainInfo, description, metadataColumns)
        }
        newImporter.actionPendingModels()

    }

}
