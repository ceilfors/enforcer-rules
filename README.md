enforcer-rules
==============

Additional custom rules for Maven enforcer.

Available extra rules:
* [requireDepMgt] - enforces that dependency management is used.
* [requireFilesContent] - enforces that the specified list of files exist and are containing the specified content.
* [matchGroupIdAndPackage] - enforces a module to contain a package that matches its group id.
* [matchArtifactIdAndBaseDir] - enforces that the pom's artifact id and the base directory name are the same.

For more sample usages, refer to integration test scenarios in src/it folder.

[requireDepMgt]: https://github.com/ceilfors/enforcer-rules/wiki/requireDepMgmt
[requireFilesCOntent]: https://github.com/ceilfors/enforcer-rules/wiki/requireFilesContent
[matchGroupIdAndPackage]: https://github.com/ceilfors/enforcer-rules/wiki/matchGroupIdAndPackage
[matchArtifactIdAndBaseDir]: https://github.com/ceilfors/enforcer-rules/wiki/matchArtifactIdAndBaseDir
