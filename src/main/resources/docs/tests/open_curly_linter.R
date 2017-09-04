##Pattern: open_curly_linter

# Pass
f <- function(x) {
    x
}

##Warn: open_curly_linter
f <- function(x) { x
}

##Warn: open_curly_linter
f <- function(x)
{
    x
}
