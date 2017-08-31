setwd('~/Documents/General/data_days/summer_2017/codacy-lintr/')
library(jsonlite)

args_json <- fromJSON('codacy-test.json')
# args_json <- args[1]

# names(args_json)
# args_json$source
# args_json$configuration
# args_json$files
# args_json$options
# args_json$specification

for(file in args_json$files){
  # file = args_json$files[1]
  print(file)
  ext = toupper(strsplit(file, '\\.')[[1]][-1])
  if(ext=="R"){
    print('   linting...')
    lintr::lint(file)
  }
}
>>>>>>> fbd8cb4624fed2c16a7b2c48223b12bc048a9c83
