# Job Scraper
Scrapes job boards (currently only supports [Glassdoor.com](glassdoor.com) for jobs with support for exclusion filters and dumps the results into a spreadsheet for easy filtering.



## Usage

1. If you need to exclude certain job titles from the results, create a file `excludejobtitles.txt` and add one line for every phrase you wish to exclude. See [excludejobtitles.txt.example](excludejobtitles.txt.example) for examples.
2. Run
    ```
       ./gradlew glassdoor
    ```

    Replace `./gradlew` with `gradlew.bat` in the above examples if you're on Windows.
3. Enter the ***Job Title*** and ***Location***.
4. The tool will launch chrome, load log into glassdoor.com and start crawling through all pages dumping results into **GlassdoorJobs.xlsx**.

## TODO
- [] Support for LinkedIn Jobs
- [] Support for Indeed.com
- [] Apply some sort of similarity co-efficient on job description based on profile being searched.