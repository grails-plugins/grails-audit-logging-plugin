/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/
package grails.plugins.orm.auditable

class AuditLogEventController {
    protected Class AuditLogEvent = AuditLogListenerUtil.auditDomainClass

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def index() {
        whenEnabled {
            redirect(action: 'list', params: params)
        }
    }

    def list() {
        whenEnabled {
            if (!params.max) {
                params.max = 10
            }

            [auditLogEventInstanceList: AuditLogEvent.list(params), auditLogEventInstanceTotal: AuditLogEvent.count()]
        }
    }

    def show() {
        whenEnabled {
            def auditLogEvent = AuditLogEvent.get(params.id)
            if (auditLogEvent == null) {
                flash.message = "AuditLogEvent not found with id ${params.id}"
                redirect(action: 'list')
                return
            }
            [auditLogEventInstance: auditLogEvent]
        }
    }

    def delete() {
        whenEnabled {
            redirect(action: 'list')
        }
    }

    def edit() {
        whenEnabled {
            redirect(action: 'list')
        }
    }

    def update() {
        whenEnabled {
            redirect(action: 'list')
        }
    }

    def create() {
        whenEnabled {
            redirect(action: 'list')
        }
    }

    def save() {
        whenEnabled {
            redirect(action: 'list')
        }
    }

    /**
     * Runs the action only when grails.plugin.auditLog.controllerEnabled is true, and responds with 404 otherwise.
     * This controller does no authorization, so it would show the audit log to anyone who can reach it.
     */
    private whenEnabled(Closure action) {
        if (!AuditLogContext.context.controllerEnabled) {
            render(status: 404)
            return null
        }
        action.call()
    }
}
