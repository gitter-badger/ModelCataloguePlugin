package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements
import spock.lang.Unroll

/**
 * Created by ladin on 28.04.14.
 */
abstract class AbstractPublishedElementControllerIntegrationSpec extends AbstractExtendibleElementControllerIntegrationSpec {

    def publishedElementService


    def "update and create new version"() {
        if (controller.readOnly) return

        String newName                  = "UPDATED NAME WITH NEW VERSION"
        PublishedElement another        = PublishedElement.get(anotherLoadItem.id)
        String currentName              = another.name
        Integer currentVersionNumber    = another.versionNumber
        Integer numberOfCurrentVersions = another.countVersions()

        when:
        controller.request.method       = 'PUT'
        controller.params.id            = another.id
        controller.params.newVersion    = true
        controller.request.json         = [name: newName]
        controller.response.format      = "json"

        controller.update()

        PublishedElement oldVersion    = PublishedElement.findByLatestVersionAndVersionNumber(another.latestVersion ?: another, currentVersionNumber)

        def json = controller.response.json

        then:
        json.versionNumber          == currentVersionNumber + 1
        json.name                   == newName

        another.countVersions()     == numberOfCurrentVersions + 1

        oldVersion.versionNumber    == currentVersionNumber
        oldVersion.name             == currentName
        oldVersion.name             != json.name
    }


//    def "get archived relationships"() {
//        if (controller.readOnly) return
//
//        String newName                  = "UPDATED NAME WITH NEW VERSION 2"
//        PublishedElement another1        = PublishedElement.get(anotherLoadItem.id)
//        PublishedElement another       = PublishedElement.get(loadItem.id)
//        String currentName              = another.name
//        String currentModelCatalogueId  = another.modelCatalogueId
//        Integer currentVersionNumber    = another.versionNumber
//        Integer numberOfCurrentVersions = another.countVersions()
//
//        another.addToRelatedTo(another1)
//        def archived = publishedElementService.archiveAndIncreaseVersion(another)
//
//
//        when:
//
//        controller.params.id            = another1.id
//        controller.params.archived = 'true'
//        controller.response.format      = "json"
//
//        controller.incoming()
//
//        def json = controller.response.json
//
//        then:
//
//        json.total == 2
//        json.list.get(1).relation.archived == true
//
//    }

    @Unroll
    def "get json history: #no where max: #max offset: #offset\""() {
        CatalogueElement first = CatalogueElement.get(loadItem.id)
        createArchiveVersions(first)

        when:
        controller.params.id = first.id
        controller.params.offset = offset
        controller.params.max = max
        controller.response.format = "json"
        controller.history(max)
        def json = controller.response.json

        recordResult "history$no", json


        then:
        checkJsonCorrectListValues(json, total, size, offset, max, next, previous)
        json.itemType == resource.name

        // TODO: add more verification

        where:
        [no, size, max, offset, total, next, previous] << getHistoryPaginationParameters("/${resourceName}/${loadItem.id}/history")
    }


    @Unroll
    def "get xml history: #no where max: #max offset: #offset"() {
        CatalogueElement first = CatalogueElement.get(loadItem.id)
        createArchiveVersions(first)

        when:
        controller.params.id = first.id
        controller.params.offset = offset
        controller.params.max = max
        controller.response.format = "xml"
        controller.history(max)
        def xml = controller.response.xml

        recordResult "history$no", xml

        then:
        checkXmlCorrectListValues(xml, total, size, offset, max, next, previous)
        xml.element.size() == size


        // todo add more verification

        where:
        [no, size, max, offset, total, next, previous] << getHistoryPaginationParameters("/${resourceName}/${loadItem.id}/history")
    }

    def getHistoryPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, 12, "${baseLink}?max=10&sort=versionNumber&order=desc&offset=10", ""],
                [2, 5, 5, 0, 12, "${baseLink}?max=5&sort=versionNumber&order=desc&offset=5", ""],
                [3, 5, 5, 5, 12, "${baseLink}?max=5&sort=versionNumber&order=desc&offset=10", "${baseLink}?max=5&sort=versionNumber&order=desc&offset=0"],
                [4, 4, 4, 8, 12, "", "${baseLink}?max=4&sort=versionNumber&order=desc&offset=4"],
                [5, 2, 10, 10, 12, "", "${baseLink}?max=10&sort=versionNumber&order=desc&offset=0"],
                [6, 2, 2, 10, 12, "", "${baseLink}?max=2&sort=versionNumber&order=desc&offset=8"]
        ]
    }

    void createArchiveVersions(PublishedElement el) {
        while (el.versionNumber != 12) {
            publishedElementService.archiveAndIncreaseVersion(el)
        }
    }




}
