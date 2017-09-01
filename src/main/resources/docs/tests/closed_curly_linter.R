# Pattern: closed_curly_linter

# Pass
if (is.null(ylim)) {
  ylim <- c(0, 0.06)
}

# Fail A
if (is.null(ylim)) {
  ylim <- c(0, 0.06)}

# Fail B
if (is.null(ylim)) {ylim <- c(0, 0.06)}

