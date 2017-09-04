##Pattern: undesirable_function_linter

# Pass
f <- function(x) {
    x
}

##Warn: undesirable_function_linter
f <- function(x) {
    return(x)
}
