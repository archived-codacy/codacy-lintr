library(jsonlite)

# arg[1] should be the result of system call from lintr.scala
# codacy-test.json is the result of a single call
args <- commandArgs(trailingOnly = TRUE)
args_json <- fromJSON(args[1])

# populate df with every error from every R file
errors_df <- data.frame('filename'=c(),
                        'message'=c(),
                        'patternId'=c(),
                        'line'=c())
for(i in 1:length(args_json$files)){
  file = args_json$files[i]
  ext = toupper(strsplit(file, '\\.')[[1]][-1])
  if(ext=="R"){
    # lint the file
    error_obj <- lintr::lint(file)

    for(i in 1:length(error_obj)){
      y <- error_obj[[i]]
      error_df <- data.frame('filename'=y$filename,
                           'message'=y$message,
                           'patternId'=y$linter,
                           'line'=y$line_number)
      errors_df <- rbind(errors_df, error_df)
    }
  }
}

# filter the errors based on configuration (originating from src/.codacy.json)
ndx <- errors_df$patternId %in% args_json$configuration$patternId
errors_final_df <- errors_df[ndx,]

# print each error as a jsonline to stdout
for(i in 1:nrow(errors_final_df)){
  clean_row <-gsub('\\[|\\]', '', toJSON(errors_final_df[i,]))
  print(clean_row)
}
