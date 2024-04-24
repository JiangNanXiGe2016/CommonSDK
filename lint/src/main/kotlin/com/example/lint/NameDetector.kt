package com.example.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import java.util.EnumSet

/**
 *
 *
 * **/
class NameDetector:Detector(), SourceCodeScanner {

companion object{
    private fun issue(
        id: String,
        briefDescription: String,
        explanation: String,
    ): Issue = Issue.create(
        id = id,
        briefDescription = briefDescription,
        explanation = explanation,
        category = Category.TESTING,
        priority = 5,
        severity = Severity.WARNING,
        implementation = Implementation(
            NameDetector::class.java,
            EnumSet.of(Scope.JAVA_FILE, Scope.TEST_SOURCES),
        ),
    )

    @JvmField
    val PREFIX: Issue = issue(
        id = "TestMethodPrefix",
        briefDescription = "Test method starts with `test`",
        explanation = "Test method should not start with `test`.",
    )

    @JvmField
    val FORMAT: Issue = issue(
        id = "TestMethodFormat",
        briefDescription = "Test method does not follow the `given_when_then` or `when_then` format",
        explanation = "Test method should follow the `given_when_then` or `when_then` format.",
    )
}
}