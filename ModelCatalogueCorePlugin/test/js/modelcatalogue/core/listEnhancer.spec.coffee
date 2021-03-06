describe "mc.core.listEnhancer", ->

  rest                  = null
  enhance               = null
  $httpBackend          = null
  $rootScope            = null
  modelCatalogueApiRoot = null

  beforeEach module "mc.core.listEnhancer"

  beforeEach inject (_rest_, _enhance_, _$httpBackend_, _$rootScope_, _modelCatalogueApiRoot_) ->
    rest                  = _rest_
    $httpBackend          = _$httpBackend_
    $rootScope            = _$rootScope_
    modelCatalogueApiRoot = _modelCatalogueApiRoot_
    enhance               = _enhance_

  it "can list resource", ->
    $httpBackend
    .when("GET", '/foo')
    .respond(fixtures.valueDomain.list1)

    result = null
    error  = null
    enhance(rest(method: 'GET', url: '/foo')).then( (_result_) ->
      result = _result_
    , (_error_) ->
      error = _error_
    )

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toBeDefined()
    expect(result.total).toBe(12)
    expect(result.page).toBe(10)
    expect(result.size).toBe(10)
    expect(result.offset).toBe(0)
    expect(result.currentPage).toBe(1)
    expect(result.list).toBeDefined()
    expect(result.list.length).toBe(10)
    expect(angular.isFunction(result.next)).toBeTruthy()
    expect(result.next.size).toBe(2)
    expect(angular.isFunction(result.previous)).toBeTruthy()
    expect(result.previous.size).toBe(0)
    expect(angular.isFunction(result.goto)).toBeTruthy()

    emptyResult = null

    result.previous().then((_result_) -> emptyResult = _result_)

    expect(emptyResult).toBeNull()

    $rootScope.$apply();

    expect(emptyResult).toBeDefined()
    expect(emptyResult.total).toBe(12)
    expect(emptyResult.page).toBe(10)
    expect(emptyResult.size).toBe(0)
    expect(emptyResult.offset).toBe(0)
    expect(emptyResult.currentPage).toBe(1)
    expect(emptyResult.list).toBeDefined()
    expect(emptyResult.list.length).toBe(0)
    expect(emptyResult.success).toBeFalsy()


    nextList = angular.copy(fixtures.valueDomain.list1)
    nextList.size = 2
    nextList.list = nextList.list.slice(0, 2)
    nextList.offset = 10
    nextList.next = fixtures.valueDomain.list1.next.replace("offset=10", "offset=20")
    nextList.previous = "/valueDomain/?max=10&offset=0"

    $httpBackend
    .when("GET", "#{modelCatalogueApiRoot}/valueDomain/?max=10&offset=10")
    .respond(nextList)

    nextResult = null
    nextError  = null

    result.next().then( (_result_) ->
      nextResult = _result_
    , (_error_) ->
      nextError = _error_
    )

    expect(nextResult).toBeNull()

    $httpBackend.flush()

    expect(nextResult).toBeDefined()
    expect(nextResult.total).toBe(12)
    expect(nextResult.page).toBe(10)
    expect(nextResult.size).toBe(2)
    expect(nextResult.offset).toBe(10)
    expect(nextResult.currentPage).toBe(2)
    expect(nextResult.list).toBeDefined()
    expect(nextResult.list.length).toBe(2)
    expect(angular.isFunction(nextResult.next)).toBeTruthy()
    expect(angular.isFunction(nextResult.previous)).toBeTruthy()

    $httpBackend
    .when("GET", "#{modelCatalogueApiRoot}/valueDomain/?max=10&offset=0")
    .respond(fixtures.valueDomain.list1)

    result = null
    error  = null
    nextResult.previous().then( (_result_) ->
      result = _result_
    , (_error_) ->
      error = _error_
    )

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toBeDefined()
    expect(result.total).toBe(12)
    expect(result.page).toBe(10)
    expect(result.size).toBe(10)
    expect(result.offset).toBe(0)
    expect(result.currentPage).toBe(1)
    expect(result.list).toBeDefined()
    expect(result.list.length).toBe(10)

    gotoResult = null
    error  = null
    result.goto(2).then( (_result_) ->
      gotoResult = _result_
    , (_error_) ->
      error = _error_
    )

    expect(gotoResult).toBeNull()

    $httpBackend.flush()

    expect(gotoResult).toBeDefined()
    expect(gotoResult.total).toBe(12)
    expect(gotoResult.page).toBe(10)
    expect(gotoResult.size).toBe(2)
    expect(gotoResult.offset).toBe(10)
    expect(gotoResult.currentPage).toBe(2)
    expect(gotoResult.list).toBeDefined()
    expect(gotoResult.list.length).toBe(2)


    $httpBackend
    .when("GET", "#{modelCatalogueApiRoot}/valueDomain/?max=10&offset=10&order=asc&sort=name")
    .respond(nextList)

    reloadResult = null
    error  = null
    gotoResult.reload(sort: 'name', order: 'asc').then( (_result_) ->
      reloadResult = _result_
    , (_error_) ->
      error = _error_
    )

    expect(reloadResult).toBeNull()

    $httpBackend.flush()

    expect(reloadResult).toBeDefined()
    expect(reloadResult.total).toBe(12)
    expect(reloadResult.page).toBe(10)
    expect(reloadResult.size).toBe(2)
    expect(reloadResult.offset).toBe(10)
    expect(reloadResult.currentPage).toBe(2)
    expect(reloadResult.list).toBeDefined()
    expect(reloadResult.list.length).toBe(2)



    listEnhancer = enhance.getEnhancer('list')

    emptyList = listEnhancer.createEmptyList('relationships')
    expect(emptyList.total).toBe(0)
    expect(emptyList.size).toBe(0)
    expect(emptyList.list).toEqual([])
    expect(emptyList.next.size).toBe(0)
    expect(emptyList.previous.size).toBe(0)
    expect(emptyList.empty).toBeTruthy()
    expect(emptyList.itemType).toBe('relationships')

    singleItem    = {foo: 'bar', elementType: 'the.type'}
    singletonList = listEnhancer.createSingletonList(singleItem)
    expect(singletonList.total).toBe(1)
    expect(singletonList.size).toBe(1)
    expect(singletonList.list).toEqual([singleItem])
    expect(singletonList.next.size).toBe(0)
    expect(singletonList.previous.size).toBe(0)
    expect(singletonList.itemType).toBe('the.type')

    theList     = [{one: 'one'}, {two: 2}, {3: 'three'}]
    wrappedList = listEnhancer.createArrayList(theList, 'the.list.type')
    expect(wrappedList.total).toBe(3)
    expect(wrappedList.size).toBe(3)
    expect(wrappedList.list).toEqual(theList)
    expect(wrappedList.next.size).toBe(0)
    expect(wrappedList.previous.size).toBe(0)
    expect(wrappedList.itemType).toBe('the.list.type')


