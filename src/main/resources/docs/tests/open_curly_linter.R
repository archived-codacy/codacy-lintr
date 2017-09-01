# Pattern: open_curly_linter

# Pass
f <- function(x) {
    x
}

# Fail A
f <- function(x) { x
}

# Fail B
f <- function(x)
{
    x
}
