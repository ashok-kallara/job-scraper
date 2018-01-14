import org.junit.Assert
import org.junit.Test

class JobScraperTest {

    @Test
    void testSpreadsheetCreation() {
        Set<JobDetails> jobDetailsSet = [] as Set
        jobDetailsSet << new JobDetails(jobTitle: "Software Architect 1", companyName: "XYZ Corp", location: "Atlanta, US", url:"http://google.com", salarayRange: "N/A")
        jobDetailsSet << new JobDetails(jobTitle: "Software Architect 2", companyName: "ABC Corp", location: "Atlanta, US", url:"http://google.com", salarayRange: "N/A")
        jobDetailsSet << new JobDetails(jobTitle: "Software Architect 3", companyName: "123 Corp", location: "Atlanta, US", url:"http://google.com", salarayRange: "N/A")
        jobDetailsSet << new JobDetails(jobTitle: "Software Architect 4", companyName: "DEF Corp", location: "Atlanta, US", url:"http://google.com", salarayRange: "N/A")
        def outputFile = "${System.properties['java.io.tmpdir']}/test.xlsx"
        println "Writing to: ${outputFile}"
        new DataPersistor().writeToExcel(jobDetailsSet, outputFile)
    }

    @Test
    void testExclusionFilters() {
        JobScraper gdInstance = new JobScraper(exclusions: ['systems architect', 'web architect'] as Set)

        JobDetails validJobDetails = new JobDetails(jobTitle: "Principal Architect", companyName: "XYZ Corp", location: "Atlanta, US", url:"http://google.com", salarayRange: "N/A")
        boolean isValidTitle = gdInstance.isValidJobTitle(validJobDetails)
        Assert.assertEquals(true, isValidTitle)

        //invalid since title has phrase "Web Architect" in it.
        JobDetails invalidJobDetails = new JobDetails(jobTitle: "Principal Web Architect", companyName: "XYZ Corp", location: "Atlanta, US", url:"http://google.com", salarayRange: "N/A")
        isValidTitle = gdInstance.isValidJobTitle(invalidJobDetails)
        Assert.assertEquals(false, isValidTitle)
    }

    @Test
    void testExclusionFilterSet() {
        def exclusionFilterFile = new File("${System.properties['java.io.tmpdir']}/excludejobtitles.txt")

        exclusionFilterFile.createNewFile()
        exclusionFilterFile << 'Title1\n'
        exclusionFilterFile << 'Title2\n'
        exclusionFilterFile << 'Title3\n'

        def excludeJobTitles = new JobScraper().getJobTitleExclusions(exclusionFilterFile)

        Assert.assertEquals(['Title1', 'Title2', 'Title3'] as Set, excludeJobTitles)
    }
}
