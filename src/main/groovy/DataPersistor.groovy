import com.jameskleeh.excel.ExcelBuilder
import com.jameskleeh.excel.Font
import org.apache.poi.common.usermodel.Hyperlink
import org.apache.poi.common.usermodel.HyperlinkType
import org.apache.poi.xssf.usermodel.XSSFCell

class DataPersistor {
    def writeToExcel(Set<JobDetails> jobDetailsSet, String outputFile) {
        File file = new File(outputFile)

        ExcelBuilder.output(new FileOutputStream(file)) {
            sheet ("Jobs", [width: 40]){
                def helper = wb.getCreationHelper()
                row {
                    cell("Job Title", [font: Font.BOLD])
                    cell("Company Name", [font: Font.BOLD])
                    cell("Location", [font: Font.BOLD])
                    cell("Link", [font: Font.BOLD])
                }

                jobDetailsSet.each { JobDetails jd ->
                    row {
                        cell(jd.jobTitle)
                        cell(jd.companyName)
                        cell(jd.location)

                        Hyperlink link = helper.createHyperlink(HyperlinkType.URL)
                        link.setAddress(jd.url)
                        XSSFCell customCell = cell(jd.url)
                        customCell.setHyperlink(link)
                    }
                }
            }
        }
    }
}
