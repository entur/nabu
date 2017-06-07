<html>

<body>
<h4>${message("notification.email.header")}</h4>

<#if totalNotificationsCnt gt emailNotificationMaxEvents>
${message("notification.email.truncated","${totalNotificationsCnt}","${emailNotificationMaxEvents}")}
</#if>

    <#list jobEvents as jobDomain,jobEventsForDomain>
        <h3>${message("notification.email.jobevent.domain.${jobDomain}")}</h3>
        <table style="text-align:left">
            <tr>
                <th>${message("notification.email.jobevent.header.organisation")}</th>
                <th>${message("notification.email.jobevent.header.action")}</th>
                <th>${message("notification.email.jobevent.header.state")}</th>
                <th>${message("notification.email.jobevent.header.filename")}</th>
                <th>${message("notification.email.jobevent.header.eventtime")}</th>
            </tr>

            <#list jobEventsForDomain as jobEvent>
                <tr>
                    <td>${providers[""+jobEvent.providerId]!""}</td>
                    <td><#if jobEvent.action?has_content>${message("notification.email.jobevent.action.${jobEvent.action}")}</#if></td>
                    <td>${message("notification.email.jobevent.jobstate.${jobEvent.state}")}</td>
                    <td>${jobEvent.name}</td>
                    <td>${jobEvent.eventTime}</td>
                </tr>
            </#list>
        </table>
    </#list>

    <#list crudEvents as entityType,crudEventsForType>
        <h3>${message("notification.email.crudevent.entitytype.${entityType}")}</h3>
        <table style="text-align:left">
            <tr>
                <th>${message("notification.email.crudevent.header.classifier")}</th>
                <th>${message("notification.email.crudevent.header.action")}</th>
                <th>${message("notification.email.crudevent.header.name")}</th>
                <th>${message("notification.email.crudevent.header.id")}</th>
                <th>${message("notification.email.crudevent.header.version")}</th>
                <th>${message("notification.email.crudevent.header.validFrom")}</th>
                <th>${message("notification.email.crudevent.header.change")}</th>
                <th>${message("notification.email.crudevent.header.oldValue")}</th>
                <th>${message("notification.email.crudevent.header.newValue")}</th>
            </tr>


            <#list crudEventsForType as crudEvent>
                <tr>
                    <td>${crudEvent.entityClassifier}</td>
                    <td><#if crudEvent.action?has_content>${message("notification.email.crudevent.action.${crudEvent.action}")}</#if></td>
                    <td>${crudEvent.name}</td>
                    <td>${crudEvent.externalId!""}</td>
                    <td>${crudEvent.version}</td>
                    <td>${crudEvent.eventTime}</td>
                    <td><#if crudEvent.changeType?has_content>${message("notification.email.crudevent.changetype.${crudEvent.changeType}")}</#if></td>
                    <td>${crudEvent.oldValue!""}</td>
                    <td>${crudEvent.newValue!""}</td>
                </tr>
            </#list>
        </table>
    </#list>
<p>
<span>${message("notification.email.footer","${notificationConfigurationLink}")}</span>
</p>
</body>
</html>