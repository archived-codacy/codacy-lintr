# Pattern: pipe_continuation_linter

# Pass
df %>%
    mutate(sevens = 7)

# Pass B
df %>% mutate(sevens = 7)

# Fail
df %>% mutate(sevens = 7) %>%
    mutate(eights = 8)
