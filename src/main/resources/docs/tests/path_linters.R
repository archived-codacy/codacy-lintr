##Pattern: path_linters

# Pass
path <- "src/my-file.csv"

##Warn: path_linters
path <- "/var/my-file.csv"

##Warn: path_linters
path <- "C:\\System\\my-file.csv"

##Warn: path_linters
path <- "~/docs/my-file.csv"
