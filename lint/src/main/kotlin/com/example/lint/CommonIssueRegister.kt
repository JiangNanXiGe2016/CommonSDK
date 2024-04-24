package com.example.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue

class CommonIssueRegister : IssueRegistry() {
    override val issues = listOf(NameDetector.FORMAT, NameDetector.PREFIX)
}