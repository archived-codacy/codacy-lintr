# Pattern: seq_linter

v <- c(1, 1, 1)

# Pass
for (i in 1:length(v)) {
  print(i)
}


# Fail
v <- c(1, 1, 1)
for (i in seq_along(v)) {
  print(i)
}
