##Pattern: pipe_continuation_linter

# Pass
df %>%
    mutate(sevens = 7)

# Pass B
df %>% mutate(sevens = 7)

##Warn: pipe_continuation_linter
df %>% mutate(sevens = 7) %>%
    mutate(eights = 8)
