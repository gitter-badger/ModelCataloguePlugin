package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 05/02/2014.
 */
@Mock(Model)
class ModelSpec extends Specification{

    @Unroll
    def "Model creation for #args results in #validates"()
    {

        expect:

        Model.list().isEmpty()

        when:

        Model modelInstance = new Model(args)

        modelInstance.save()

        then:

        !modelInstance.hasErrors() == validates

        where:

        validates | args
        false      | [name:"t", description: "test model description"]
        false      | [name:"t"*256, description: "test model description"]
        false      | [name:"test model", description: "t"*2001]
        true       | [name:"test model", description: "test model description"]

    }

    @Unroll
    def "create a new model and extend it to include additional metadata"(){

        expect:

        Model.list().isEmpty()

        when:

        def dataExtension = ["ip": "x.xx.xx.xx", "owner": "BRC_Informatics"]

        Model modelInstance = new Model(name: "result1", description: "this is the the result description")

        modelInstance.address =  dataExtension

        modelInstance.save()

        then:

        !modelInstance.hasErrors()
        modelInstance.address == dataExtension

    }


}
