# Code Review Extension for Polarion® ALM™

Copyright 2016 Polarion AG  
TextDiffMatchPatch.java Copyright (C) 2012 Tine Kondo  
TextDiffMatchPatch.java Copyright (C) 2006 Google Inc.  
highlight.js Copyright (C) 2006 Ivan Sagalaev  
jQuery v3.0.0 Copyright jQuery Foundation and other contributors  
Sticky-kit v1.1.2 by Leaf Corcoran  

This Polarion® ALM™ extension provides ability to do code review through Polarion portal.

## Requirements

- Polarion® ALM™ 2016 or newer
- Java 8 or newer

## Installation

1. Download packaged extension from http://extensions.polarion.com/extensions/235-code-review-in-polarion
2. Stop Polarion server.
3. Extract `com.polarion.alm.extensions.codereview.zip` into `<Polarion Installation>/polarion/extensions`.
4. Start Polarion server.

## Code Review Process in Two Simple Steps

Code reviews help spread knowledge, catch bugs in advance and reduce technical debt. Together with test-driven development or pair
programming they can greatly increase code quality and maintainability. We are in no way sorry for implementing it in Polarion and use it
while developing Polarion.

So now that you are convinced that code review is what you must have, the question is: how to approach it. There are various tools for
various version control systems, but you have Polarion and would like something integrated. Or you want to start quickly and maybe
transition to something more robust later. We will show you how to do code review in just two simple steps based on our experiences.

In Step 1 we will present the task-centric code review process suitable for all. In case you would like to have more fine-grained reviews then there is an option of commit-centric process extension described in Step 2.

### Step 1: Track tasks for review

OK, so you have decided that code review is what you need. What is absolutely necessary to define? Who is going to do the reviews and
how will the reviews be tracked?

Reviewers are pretty straightforward, as a project role for that purpose is easy to set up and quite logical. For the review tracking we
recommend a special workflow status which is not bypassable and which every implementation task must go through. In our experience the
best place for it is right before QA does the final test round/sign off on the implementation.

![Workflow](docs/workflow.png)

Lastly we recommend to track who is doing the review in a custom field of type User enumeration (available since Polarion 2012 SR3):

![Code Reviewer field](docs/codeReviewerField.png)

To make it easier for code reviewers we have special Wiki page named “Code Review” which lists items waiting for review and items being
reviewed.

![Code review report](docs/codeReviewReport.png)

Just create new Wiki page and copy the content of the `code_review_wiki.txt` from the `docs` folder as its Wiki Markup source. Minor configuration adjustment will be needed at the top of the page:

```
 #set($codeReviewerRole = "project_code_reviewer")
 #set($awaitingCodeReviewQuery = "status:awaiting-code-review")
 #set($codeReviewerField = "codeReviewer")
 #set($projectId = $page.space.projectId)
```

- `codeReviewerRole` should contain name of the role added to code reviewers
- `awaitingCodeReviewQuery` should contain query which selects tasks waiting to be reviewed (in our case it selects all tasks which are in a status “awaiting-code-review”)
- `codeReviewerField` should contain the id of the field listing user doing the review
- `projectId` can be changed to point to different project

How it looks like when we use it? Developer commits its work and marks the task available for review by changing its status. Pack of hungry
reviewers sees the task on the top of the Code Review page and compete for who puts himself first as a reviewer of the item. When the item
is reviewed, it is pushed to QA (if developer is lucky) or returned to developer (if developer’s luck runs out). In accordance with Agile
Manifesto we promote people over process and advise reviewers to speak with the developer rather than just changing the status of the task
and writing comment.

To promote (friendly) competition there is also a Review Statistics section (will show only if [Wiki Scripting Tools](http://extensions.polarion.com/extensions/83-wiki-scripting-tools) are installed) at the bottom of
the Code Review. For fun. Mostly.

![Review statistics](docs/reviewStatistics.png)

### Step 2: Track commits for review

Your code is in Polarion’s default Subversion repository and all those fancy code reviewing tools which are online and work with Git only are
of no use for you. And you still want to be able to work on the revision level. Or your code is in Git, but you do not want to leave the comfort of Polarion portal. Well, don’t despair, we have something for you.

First add Code Review Form Extension to Form Layout Configuration (in Administration / Work Items / Form Configuration) like this:

```xml
<extension id="codereview" label="Code Review"/>
```

Second add new custom field which will hold the information about reviewed revisions and will be filled by the Code Review Form Extension.

![Reviewed Revisions field](docs/reviewedRevisionsField.png)

Finally add configuration for the Code Review extension by creating new file `.polarion/codereview/codereview.properties` inside the repository folder of your project with content similar to this:

```ini
reviewedRevisionsField=reviewedRevisions
reviewerField=codeReviewer
inReviewStatus=awaiting-code-review
successfulReviewWorkflowAction=mark_done
successfulReviewResolution=fixed
unsuccessfulReviewWorkflowAction=reopen
reviewerRole=project_code_reviewer
preventReviewConflicts=true
reviewCommentTitle=Review Comment
successfulReviewCommentTitle=Successful Review Comment
unsuccessfulReviewCommentTitle=Unsuccessful Review Comment
``` 

- `reviewedRevisionsField` should contain the id of the field that lists the reviewed revisions.
- `reviewerField` should contain the id of the field that lists the user doing the review.
- `inReviewStatus` should contain the id of the “Status” that the Code Review Form Extension allows to perform the code review.
- `successfulReviewWorkflowAction` should contain the id of the workflow action executed by the "Review all & advance" command. (This configuration is optional.)
- `successfulReviewResolution` should contain the id of the resolution option that will populate the “Resolution” field when the "Review all & advance" command is executed and the workflow requires a value for the “Resolution” field. (This configuration is optional.)
- `unsuccessfulReviewWorkflowAction` should contain the id of the workflow action executed by the "Review all & reopen" command. (This configuration is optional.)
- `reviewerRole` should contain the name of the role added for code reviewers.
- `pastReviewers` should contain the ids, (separated by spaces), of users who were reviewers in the past. (This configuration is optional.)
- `preventReviewConflicts` should be set to `true` if the Code Review Form Extension should prevent reviews done by users that are not listed in the `reviewerField`. (This configuration is optional.)
- `reviewCommentTitle` is the primary comment title used for all comments added via the extension. (This configuration is optional.) If it is not used, no comment title is displayed.
- `successfulReviewCommentTitle`  is a comment’s title when the “Review all & advance” command is clicked. (This configuration is optional.) If it is not used, `reviewCommentTitle` is used instead.
- `unsuccessfulReviewCommentTitle` is a comment’s title when the “Review all & reopen” command is clicked. (This configuration is optional). If is not used, `reviewCommentTitle` is used instead.

This is how the Code Review Form Extension looks like:

![Code review form extension](docs/extension.png)

The "Open" action leads to the special compare view of all unreviewed changes. The "Open compare of all revisions from default repository" leads also to the special compare view, but this time it shows changes from all revisions. Both these actions are able to show only changes from default Subversion repository not from any external repository.

The "Review selected" action will mark selected revisions as reviewed, "Review all" will mark all revisions as reviewed and "Review all & advance" will mark all revisions as reviewed and perform the configured workflow action.

You can write comment, which will be automatically added when using all Review buttons (Selected, All, All & advance, All & reopen).

If `preventReviewConflicts` is set then there might be an additional action "Start review" which will set current user as current reviewer.

OK, that looks easy and nice, but how to prevent some revisions to slip under the door - either not linked to any task or linked to
some already-reviewed task? For that we offer a job which can be scheduled in global Administration / Scheduler to run regularly like this:

```xml
  <job cronExpression="0 0 5 ? * *" id="codereview.checker" name="CodeReviewDemo Code Review Checker" scope="project:codereviewdemo">
   <notificationSubjectPrefix>[codereviewdemo]</notificationSubjectPrefix>
    <notificationSender>example@example.com</notificationSender>
    <notificationReceivers>
     	<notificationReceiver>example@example.com</notificationReceiver>
   	</notificationReceivers>
	<repositoryLocations>
		<repositoryLocation>
			<locationPath>/codereviewdemo/trunk</locationPath>
			<revision>10</revision>
		</repositoryLocation>
		<repositoryLocation>
			<repositoryName>codereviewdemo:codeReviewExtension</repositoryName>
		</repositoryLocation>
	</repositoryLocations>
	<permittedItemsQuery>project:codereviewdemo AND type:(task issue)</permittedItemsQuery>
  </job>
```

- `notificationSubjectPrefix`, `notificationSender` and `notificationReceivers` should define the subject, sender and
receivers for the notification mail
- `repositoryLocations` should define all locations which should be checked in nested `repositoryLocation` elements which can point to:
 - the location in the default Subversion repository with `locationPath` pointing to the root of the code source tree (e.g. trunk or some branch)  and `revision` set to starting revision of the code review - useful if branch is created and you don’t want to go before the
branch point (defaults to first revision of the `locationPath`); or
 - the external repository with `repositoryName` containing id of the external repository.
- `permittedItemsQuery` should define query matching all Work Items which are permitted to have revisions from checked locations (this configuration is optional)

Notification is sent when some revision is not linked at all or is linked to resolved item with this revision not listed as reviewed.

![Notification](docs/notification.png)

Additionally you can configure Code Review extension so that the checker job will report also items which have linked revisions, but do not have time points:

```ini
unresolvedWorkItemWithRevisionsNeedsTimePoint=true
```

All of this looks very good, but what if some people refuse to do code reviews although they should? For that we offer another job which
can be scheduled in global Administration / Scheduler to run regularly like this:

```xml
  <job cronExpression="0 0 13 ? * *" id="codereview.assigner" name="CodeReviewDemo Code Review Assigner" scope="project:codereviewdemo">
    <reviewerRole>project_code_reviewer</reviewerRole>
    <reviewedItemsQuery>project:codereviewdemo AND type:(task issue) AND updated:$today$</reviewedItemsQuery>
    <toBeReviewedItemsQuery>project:codereviewdemo AND type:(task issue) AND status:awaiting-code-review AND NOT HAS_VALUE:codeReviewer</toBeReviewedItemsQuery>
  </job>
```

- `reviewerRole` should contain the name of the role added for code reviewers (can be different from the role used in the Code Review extension if you want just some people to be considered for assignment).
- `reviewedItemsQuery` should define query matching all Work Items which should be considered when counting number of reviews done today.
- `toBeReviewedItemsQuery` should define query matching all Work Items which need code reviewer.

Code reviewers are assigned based on the number of reviews they have done today - less reviews means higher chance of being picked.

### Bonus step: A bit of automation 

Let's say that your source code contains also documentation and you have decided that it does not require another pair of eyes looking at the documentation source. Such items can be almost automatically reviewed by Polarion.

First create new workflow action which will bypass the code review state, add workflow condition "FastTrackReviewPermitted" and workflow function "FastTrackReview" to that action and add more configuration for the Code Review extension:

```ini
fastTrackPermittedLocationPattern=/codereviewdemo/trunk/docs/.*
fastTrackReviewer=fastTrack
```

- `fastTrackPermittedLocationPattern` should contain regular expression which matches paths which can be reviewed automatically
- `fastTrackReviewer` should contain the id of the user which will be used as a reviewer (the user does not have to be a real Polarion user)

Workflow action will be enabled only if all linked revisions changed only paths permitted by `fastTrackPermittedLocationPattern`. If the action is performed then the revisions will be reviewed by the `fastTrackReviewer` and not the user who performed the action.

## Source code

Download sources from GitHub: https://github.com/Polarion/com.polarion.alm.extensions.codereview
