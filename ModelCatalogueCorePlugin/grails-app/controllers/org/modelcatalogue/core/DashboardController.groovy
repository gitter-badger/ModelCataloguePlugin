package org.modelcatalogue.core

import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionState
import org.modelcatalogue.core.actions.Batch

class DashboardController {

    static responseFormats = ['json', 'xml', 'xlsx']
    def dataArchitectService

    def index() {

        def uninstantiatedDataElements = dataArchitectService.uninstantiatedDataElements(params)

        def model = [
                totalDataElementCount: DataElement.count(),
                draftDataElementCount:DataElement.countByStatus(PublishedElementStatus.DRAFT),
                finalizedDataElementCount:DataElement.countByStatus(PublishedElementStatus.FINALIZED),
                totalModelCount:Model.count(),
                draftModelCount:Model.countByStatus(PublishedElementStatus.DRAFT),
                finalizedModelCount:Model.countByStatus(PublishedElementStatus.FINALIZED),
                totalDataSetCount:Classification.count(),
                pendingActionCount:Action.countByState(ActionState.PENDING),
                failedActionCount:Action.countByState(ActionState.FAILED),
                batchCount:Batch.count(),
                uninstantiatedDataElementCount: uninstantiatedDataElements.total,
                relationshipTypeCount:RelationshipType.count(),
                measurementUnitCount:MeasurementUnit.count(),
                dataTypeCount:DataType.count(),
                valueDomainCount:ValueDomain.count()
                ]
        respond model
    }
}
