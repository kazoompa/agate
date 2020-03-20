<!DOCTYPE html>
<head lang="en">
  <meta charset="UTF-8"/>
  <title></title>
</head>
<body
  style="margin: 0;font-family: &quot;Lato&quot;,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size: 15px;line-height: 1.5;color: #2c3e50;background-color: #ffffff;">

<div class="well"
  style="min-height: 20px;padding: 19px;margin-bottom: 20px;background-color: #ecf0f1;border: 1px solid transparent;border-radius: 4px;-webkit-box-shadow: none;box-shadow: none;">
  <div style="margin: auto;max-width: 700px;">
    <div
      style="margin-bottom: 21px;background-color: #ffffff;border-radius: 4px;-webkit-box-shadow: 0 1px 1px rgba(0,0,0,0.05);box-shadow: 0 1px 1px rgba(0,0,0,0.05);border: 1px solid #18bc9c;">
      <div
        style="padding: 10px 15px;border-top-right-radius: 3px;border-top-left-radius: 3px;color: #ffffff;background-color: #18bc9c;border-color: #18bc9c;">
        <h3 style="font-family: &quot;Lato&quot;,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-weight: 400;line-height: 1.1;color: inherit;margin-top: 0;margin-bottom: 0;font-size: 17px;" th:text="#{mica.email.studyStatusChanged.title(${organization})}">
          <span>(organization)</span> - Study
        </h3>
      </div>
      <div class="panel-body" style="padding: 15px;">
        <p style="margin: 0 0 10px;" th:text="#{email.generic.presentation(${user.firstName}, ${user.lastName})}">
          Dear <span>(first name)</span> <span>(last name)</span>,
        </p>
        <div th:switch="${status}" style="margin: 0 0 10px;">
          <p th:case="DRAFT" th:utext="#{mica.email.studyStatusChanged.body.draft(${documentId})}">
            The study <strong>(document id)</strong> is ready to be edited.
          </p>
          <p th:case="UNDER_REVIEW" th:utext="#{mica.email.studyStatusChanged.body.underReview(${documentId})}">
            The study <strong>(document id)</strong> is ready to be reviewed. You can now approve or reject its publication.
          </p>
          <p th:case="'DELETED'" th:utext="#{mica.email.studyStatusChanged.body.deleted(${documentId})}">
            The study <strong>(document id)</strong> was marked as being deleted. You can now delete it permanently.
          </p>
          <p th:case="*" th:utext="#{mica.email.studyStatusChanged.body.other(${documentId}, ${status})}">
            The study <strong>(document id)</strong> status was changed to <strong>(status)</strong>.
          </p>
        </div>
        <p style="margin: 0 0 10px;">
        </p><p style="margin: auto;text-align: center;">
        <a href="" th:href="@{${publicUrl} + '/#/' + ${documentType} + '/' + ${documentId}}" target="_blank"
          style="color: #ffffff;text-decoration: none;display: inline-block;margin-bottom: 0;font-weight: normal;text-align: center;vertical-align: middle;-ms-touch-action: manipulation;touch-action: manipulation;cursor: pointer;background: #2c3e50 none;white-space: nowrap;padding: 10px 15px;font-size: 15px;line-height: 1.5;border-radius: 4px;-webkit-user-select: none;-moz-user-select: none;-ms-user-select: none;user-select: none;border: 1px solid #2c3e50;" th:text="#{mica.email.studyStatusChanged.link}">View Study</a>
      </p>
      </div>
    </div>
    <p class="help-block" style="display: block;margin: 5px 0 10px;color: #597ea2;" th:text="#{email.generic.message}">
      This is a generated email. Please do not reply.
    </p>
  </div>
</div>

</body>
</html>
