package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.util.Mappings


class MappingsMarshaller extends ListWrapperMarshaller {

    MappingsMarshaller() {
        super(Mappings)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(Object mappings) {
        def ret = super.prepareJsonMap(mappings)
        ret.list = mappings.items.collect {
            [id: it.id, source: CatalogueElementMarshallers.minimalCatalogueElementJSON(it.source),  mapping: it.mapping, destination: it.destination, removeLink: "${it.source.info.link}/mapping/${it.destination.id}", elementType: Mapping.name]
        }
        ret
    }

    @Override
    protected void buildItemsXml(Object mappings, XML xml) {
        xml.build {
            for (m in mappings.items) {
                mapping(id: m.id) {
                    mapping m.mapping
                    destination(m.destination)
                }
            }
        }
    }
}
