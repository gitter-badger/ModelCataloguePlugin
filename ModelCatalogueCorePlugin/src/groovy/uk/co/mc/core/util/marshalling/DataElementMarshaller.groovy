package uk.co.mc.core.util.marshalling

import grails.converters.JSON
import grails.converters.XML
import uk.co.mc.core.DataElement

class DataElementMarshaller implements MarshallersProvider {

    void register() {
        JSON.registerObjectMarshaller(DataElement) { DataElement element ->
            def ret = [code: element.code, versionNumber: element.versionNumber, extensions: element.extensions]
            ret.putAll(CatalogueElementMarshallers.prepareJsonMap(element))
            return ret
        }
        XML.registerObjectMarshaller(DataElement) { DataElement el, XML xml ->
            CatalogueElementMarshallers.buildXml(el, xml)
            xml.build {
                code el.code,
                versionNumber: element.versionNumber
                extensions: element.extensions

            }
        }
    }

}




