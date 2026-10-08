/* Copyright 2006-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugins.orm.auditable

import grails.config.Config
import grails.core.GrailsApplication
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.grails.config.NavigableMap
import org.grails.config.PropertySourcesConfig
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.PropertySource
/**
 * Helper methods that use dynamic Groovy
 */
@Slf4j
class ReflectionUtils {

    // set at startup
    static GrailsApplication application

    private ReflectionUtils() {
        // static only
    }

    static Object getConfigProperty(String name, config = AuditLoggingConfigUtils.auditConfig) {
        def value = config
        name.split('\\.').each { String part -> value = value."$part" }
        value
    }

    static void setConfigProperty(String name, value) {
        def config = AuditLoggingConfigUtils.auditConfig

        List parts = name.split('\\.')
        name = parts.remove(parts.size() - 1)

        parts.each { String part -> config = config."$part" }

        config."$name" = value
    }

    static List asList(Object o) {
        o ? o as List : []
    }

    static Config getApplicationConfig() {
        requireApplication().config
    }

    static ConfigObject getAuditConfig() {
        getAuditConfig(getApplicationConfig())
    }

    static ConfigObject getAuditConfig(Config grailsConfig) {
        if (grailsConfig.getProperty('auditLog', NavigableMap)) {
            log.error "Your auditLog configuration settings use the old prefix 'auditLog' but must now use 'grails.plugin.auditLog'"
        }
        grailsConfig.getProperty("grails.plugin.auditLog", NavigableMap) as ConfigObject
    }

    static Config removeAuditConfigPropertySource(Config config) {
        GrailsApplication app = findApplication()
        if (app != null && config.is(app.config)) {
            MutablePropertySources propertySources = app.mainContext.environment.propertySources
            propertySources.remove('AuditConfig')
            app.config = new PropertySourcesConfig(propertySources)
            return app.config
        }
        config
    }

    static void setAuditConfig(ConfigObject c) {
        GrailsApplication app = requireApplication()
        ConfigObject config = new ConfigObject()
        config.grails.plugin.auditLog = c

        PropertySource propertySource = new MapPropertySource('AuditConfig', [:] << config)
        MutablePropertySources propertySources = app.mainContext.environment.propertySources
        propertySources.addFirst propertySource

        app.config = new PropertySourcesConfig(propertySources)
    }

    // Do not assign the resolved application back onto the static field. Unit-test cleanup
    // closes that context and clears Holders; caching it here would keep the closed application.
    private static GrailsApplication findApplication() {
        application ?: Holders.findApplication()
    }

    private static GrailsApplication requireApplication() {
        GrailsApplication app = findApplication()
        if (app == null) {
            throw new IllegalStateException('AuditLoggingGrailsPlugin/BeanRegistrar initialization must complete before accessing audit configuration')
        }
        app
    }

}
