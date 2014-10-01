dataSource {
	pooled = true
	driverClassName = "org.h2.Driver"
	username = "sa"
	password = ""
}
hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = false
	cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
//    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
}

// environment specific settings
environments {
	development {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
//
//		dataSource {
//			dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'
//			pooled = true
//			dbCreate = "update"
//			url = "jdbc:mysql://localhost:13306/MCCOSD"
//			driverClassName = "com.mysql.jdbc.Driver"
//			username = "root"
//		}

	}
	test {
		dataSource {
			dbCreate = "update"
			url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
		}
	}
	production {
//        dataSource {
//            dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'
//			pooled = true
//			dbCreate = "create-drop"
//			url = "jdbc:mysql://localhost:13306/McProd"
//			driverClassName = "com.mysql.jdbc.Driver"
//			username = "root"
//        }

//        dataSource {
//            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
//            url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
//        }

	}
}