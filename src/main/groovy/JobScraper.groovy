import geb.Browser
import groovy.transform.Field

@Field Set<JobDetails> jobDetailSet = [] as Set<JobDetails>
@Field Set<String> exclusions = getJobTitleExclusions()
@Field DataPersistor dataPersistor = new DataPersistor()
@Field final String NUM_PAGES = 9

BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
def env = System.getProperties()

def username
//println 'JobScraper username (Optional):'
//username = br.readLine()

def password
//println 'JobScraper password (Not saved. Only used for login):'
//password = br.readLine()

def location = env['LOCATIONSEARCH']
def keyword = env['KEYWORDSEARCH']

if(!location) {
    location = System.console().readLine('#### Location Searh keyword (e.g: Atlanta):')
}

if(!keyword) {
    println '#### Keyword Searh keyword (e.g: Application Architect):'
    keyword = br.readLine()
}


Browser.drive{
//    driver.webClient.javaScriptEnabled = true
    go "https://www.glassdoor.com"

    if(username && password) {
        def signIn = $("a", text: "Sign In")
        println "signIn: ${signIn}"
        signIn.click()

        waitFor(2){ $("input", id: "signInUsername") }

        $("input", id: "signInUsername").value(username)
        $("input", id: "signInPassword").value(password)

        $("#signInBtn").click()
    }

    waitFor(4){ $("input", id: "LocationSearch") }

    $("input", id: "LocationSearch").value(location)
    $("input", id: "KeywordSearch").value(keyword)
    $("#HeroSearchButton").click()

    Thread.sleep(2000)

    def moreFilterButton = $("#DKFilters > div > div > div.filter.more.expandable", 0)
    println "### filterButton ${moreFilterButton}"
    moreFilterButton.click()

    def companyRatingSelection = $("#DKFilters > div > div > div.filter.more.expandable.expanded > div > div:nth-child(3)", 0)
    companyRatingSelection.click()

    Thread.sleep(2500)

    def threeStarAndUp = $("#DKFilters > div > div > div.filter.more.expandable.expanded > div > div.starsHeader.padSm > span.gdStars.lg > i:nth-child(3) > i", 0)
    threeStarAndUp.click()

    Thread.sleep(1500)

    def filterApply = $("#DKFilters > div > div > div.filter.more.expandable.expanded.applied > div > div.buttons.hideHH.borderBot > button", 0)
    filterApply.click()

    Thread.sleep(2500)


    scrapeJobs(delegate,  0)

    Thread.sleep(10000)


}.quit()

def scrapeJobs(context, int iterationCount) {


    def popUpCloseButton = context.$("button.mfp-close", 0)
    if(popUpCloseButton) {
        popUpCloseButton.click()
    }

    def jobListings = context.$("#MainCol > div:nth-child(1) > ul > li.jl")

    jobListings.each {
        def jobLink = it.find("a.jobLink")[1]

        println it.$("div:nth-child(2) > div.flexbox.empLoc > div")
        def (companyName, location) = it.$("div:nth-child(2) > div.flexbox.empLoc > div", 0).text().split(" – ")
        JobDetails jobDetails = new JobDetails(url: jobLink.@href, jobTitle: jobLink.text(), companyName: companyName, location: location)
        if(isValidJobTitle(jobDetails)) {
            jobDetailSet.add(jobDetails)
        }
    }

    if( context.$("#FooterPageNav > div > ul > li.next > a")) {
        context.$("#FooterPageNav > div > ul > li.next > a").click()
        Thread.sleep(4000)
        if(iterationCount < NUM_PAGES) { //don't want to go on for ever. Stop after 100 pages
            scrapeJobs(context, ++iterationCount)
        }
    }
    dataPersistor.writeToExcel(jobDetailSet, "./Jobs.xlsx")
    println jobDetailSet
}

Set<String> getJobTitleExclusions(exclusionFilterFile = new File("excludejobtitles.txt")) {
    def exclusionFilter = [] as Set<String>
    if(exclusionFilterFile.exists()) {
        exclusionFilter = exclusionFilterFile.text.split('\n') as Set
    }
    println "#### will exclude ${exclusionFilter}"
    return exclusionFilter
}

boolean isValidJobTitle(JobDetails jobDetails) {
    def retVal = exclusions?.find(){
            jobDetails.jobTitle.toLowerCase() =~ /${it.toLowerCase()}/
        }
    return !retVal
}

