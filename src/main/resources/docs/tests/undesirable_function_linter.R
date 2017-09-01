# Pattern: undesirable_function_linter

# Pass
f <- function(x) {
    x
}

# Fail
f <- function(x) {
    return(x)
}
