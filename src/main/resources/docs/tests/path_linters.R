# Pattern: path_linters

# Pass
path <- "src/my-file.csv"

# Fail A
path <- "/var/my-file.csv"

# Fail B
path <- "C:\\System\\my-file.csv"

# Fail C
path <- "~/docs/my-file.csv"
