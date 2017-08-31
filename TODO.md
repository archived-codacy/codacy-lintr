# Tasks

1. Configuration
    1. .codacy.json
        - Add `lintr` patterns
    2. (Low priority) resources/docs/description/ markdown files
    3. (Low priority) resources/docs/test/ .R files


2. R script
    1. Parse JSON blob from command line args
        - Recontruct the arguments to the apply function in src/main/scala/codacy/lintr/Lintr.scala
    2. Classify Files (.R and not .R)
    3. Call lintr on .R files
