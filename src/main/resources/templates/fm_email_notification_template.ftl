<html>
<head>
<style>
@import url(//fonts.googleapis.com/earlyaccess/notosanskannada.css);
a {
  color: #2196F3;
  text-decoration: none;
}

a:hover, a:focus {
  color: #1976D2;
}

body {
  font-family: 'Noto Sans Kannada', sans-serif;
  color: #191919;
}

p,h1,h2,h3,h4,h5, span {
  color: #191919;
}

th, td {
    padding: 5px;
    text-align: left;
}


</style>
</head>

<body>
<h4>${message("notification.email.header")}</h4>

<#if totalNotificationsCnt gt emailNotificationMaxEvents>
${message("notification.email.truncated","${totalNotificationsCnt}","${emailNotificationMaxEvents}")}
</#if>

<#list jobEvents as jobDomain,jobEventsForDomain>
<h3>${message("notification.email.jobevent.domain.${jobDomain}")}</h3>
<table>
    <tr>
        <th>${message("notification.email.jobevent.header.organisation")}</th>
        <th>${message("notification.email.jobevent.header.id")}</th>
        <th>${message("notification.email.jobevent.header.action")}</th>
        <th>${message("notification.email.jobevent.header.state")}</th>
        <th>${message("notification.email.jobevent.header.filename")}</th>
        <th>${message("notification.email.jobevent.header.eventtime")}</th>
    </tr>

    <#list jobEventsForDomain as jobEvent>
        <tr>
            <td>${providers[""+jobEvent.providerId]!""}</td>
            <#if jobLink(jobEvent)?has_content>
                <td><a href="${jobLink(jobEvent)!""}">${jobEvent.externalId!""}</a></td>
            <#else>
                <td>${jobEvent.externalId!""}</td>
            </#if>
            <td><#if jobEvent.action?has_content>${message("notification.email.jobevent.action.${jobEvent.action}")}</#if></td>
            <td>${message("notification.email.jobevent.jobstate.${jobEvent.state}")}</td>
            <td>${jobEvent.name!""}</td>
            <td>${jobEvent.eventTimeAsDate?datetime?iso_nz("Europe/Oslo")}</td>
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
        <!--<th>${message("notification.email.crudevent.header.id")}</th> -->
        <th>${message("notification.email.crudevent.header.version")}</th>
        <th>${message("notification.email.crudevent.header.validFrom")}</th>
        <th>${message("notification.email.crudevent.header.changedBy")}</th>
        <th>${message("notification.email.crudevent.header.comment")}</th>
        <th>${message("notification.email.crudevent.header.change")}</th>
        <th>${message("notification.email.crudevent.header.oldValue")}</th>
        <th>${message("notification.email.crudevent.header.newValue")}</th>
    </tr>


    <#list crudEventsForType as crudEvent>
        <tr>
            <td><#if crudEvent.entityClassifier?has_content>${message("notification.email.crudevent.eventclassifier.${entityType}.${crudEvent.entityClassifier}")}</#if></td>
            <td><#if crudEvent.action?has_content>${message("notification.email.crudevent.action.${crudEvent.action}")}</#if></td>
            <!--<td>${crudEvent.name!""}</td> -->
            <td><a href="${stopPlaceLinkPrefix!""}${crudEvent.externalId!""}">${crudEvent.name!""}</a></td>
            <td>${crudEvent.version}</td>
            <td>${crudEvent.eventTimeAsDate?datetime?iso_nz("Europe/Oslo")}</td>
            <td>${crudEvent.username!""}</td>
            <td>${crudEvent.comment!""}</td>
            <td><#if crudEvent.changeType?has_content>${message("notification.email.crudevent.changetype.${crudEvent.changeType}")}</#if></td>
            <td>${crudEvent.oldValue!""}</td>
            <td>${crudEvent.newValue!""}</td>
        </tr>
    </#list>
</table>
</#list>
<#-- Commented out as self service notification config is not yet supported <p>-->
    <#--<span>${message("notification.email.footer","${notificationConfigurationLink}")}</span>-->
<#--</p>-->
</body>
</html>