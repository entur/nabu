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
<h4>${message("new.user.email.header")} ${user.contactDetails.firstName} ${user.contactDetails.lastName}</h4>

<p>${message("new.user.email.introduction")}:</p>

${message("new.user.email.username")}: ${user.username}
<br>
<p>
${message("new.user.email.password.text")} <a href="${forgotPasswordLink}">${message("new.user.email.password.forgot.text")}</a>
</p>


<p>
${message("new.user.email.stop.place.user.guide.text")} <a href="${stopPlaceUserGuideLink}">${message("new.user.email.stop.place.user.guide.link.text")}</a>
</p>


<h4>${message("new.user.email.link.text")}: </h4>
<table>
    <tr>
        <td><a href="${stopPlaceLink}">${stopPlaceLink}</a></td> <td>${message("new.user.email.link.stopplace")}</td>
    </tr>
    <#--<tr>-->
        <#--<td><a href="${operatorLink}">${operatorLink}</a></td> <td>${message("new.user.email.link.operator")}</td>-->
    <#--</tr>-->
    <#--<tr>-->
        <#--<td><a href="${routedbLink}">${routedbLink}</a></td> <td>${message("new.user.email.link.routedb")}</td>-->
    <#--</tr>-->
    <tr>
        <td><a href="${devLink}">${devLink}</a></td> <td>${message("new.user.email.link.dev")}</td>
    </tr>
    <tr>
        <td><a href="${manualLink}">${manualLink}</a></td> <td>${message("new.user.email.link.manual")}</td>
    </tr>
    <tr>
        <td><a href="${netexProfileLink}">${netexProfileLink}</a></td> <td>${message("new.user.email.link.netexprofile")}</td>
    </tr>
    <tr>
        <td><a href="${siriProfileLink}">${siriProfileLink}</a></td> <td>${message("new.user.email.link.siriprofile")}</td>
    </tr>

</table>


<h4>${message("new.user.email.contact.info.text")}: <a href="${contactInfoEmail}">${contactInfoEmail}</a></td></h4>


</body>
</html>