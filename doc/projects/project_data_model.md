## CrowdfundingProject

Represents the main entity of a crowdfunding project.

- **id**: `string` - Unique identifier of the project.
- **title**: `string` - Title of the project.
- **requestedAmount**: `number` - Total amount of funds the project aims to collect.
- **collectedAmount**: `number` - Amount of funds that have already been collected.
- **currency**: `string` - Currency in which the funds are being raised (e.g., EUR, USD).
- **imageUrl**: `string` - URL of the project’s image.
- **risk**: `number` - Risk level of the project (e.g., 1-5).
- **owner**: `CrowdfundingProjectOwner` - Object containing the project owner’s information.
- **projectStartDate**: `Date` - Start date of the project.
- **projectEndDate**: `Date` - End date of the project.
- **numberOfBackers**: `number` - Number of backers who have supported the project.
- **description**: `string` - A brief description of the project.
- **longDescription**: `string` - A more detailed description of the project.
- **expectedProfit**: `number` - Expected profit percentage from the project.
- **minimumInvestment**: `number` - Minimum amount required to invest in the project.
- **rewards**: `ProjectReward[]` - List of rewards offered for different levels of investment.
- **projectVideoUrl**: `string` - URL of the project's video.

---

## CrowdfundingProjectOwner

Represents the owner of the crowdfunding project.

- **name**: `string` - The name of the project owner.
- **imageUrl**: `string` - URL of the owner’s image.

---

## CrowdfundingProjectChart

A base interface for representing charts used in the project.

- **id**: `string` - Unique identifier of the chart.
- **type**: `string` - Type of the chart (e.g., "donut_chart", "area_chart").
- **name**: `string` - Name of the chart.

### Donut Chart (CrowdfundingProjectDonutChart)

Represents a donut chart used in the project.

- **type**: `"donut_chart"` - The type is fixed as `"donut_chart"`.
- **elements**: `DonutElement[]` - Elements to be displayed in the donut chart.

### DonutElement

- **label**: `string` - The label for the donut chart segment.
- **value**: `number` - The value for the corresponding label in the donut chart.

### Area Chart (CrowdfundingProjectAreaChart)

Represents an area chart used in the project.

- **type**: `"area_chart"` - The type is fixed as `"area_chart"`.
- **series**: `AreaChartSeries[]` - Series of data for the area chart.

### AreaChartSeries

- **name**: `string` - The name of the series.
- **elements**: `AreaChartElement[]` - Elements within the series.

### AreaChartElement

- **label**: `string` - Label for the data point.
- **value**: `number` - Value for the corresponding label.

### Bar Chart (CrowdfundingProjectBarChart)

Represents a bar chart used in the project.

- **type**: `"bar_chart"` - The type is fixed as `"bar_chart"`.
- **series**: `BarChartSeries[]` - Series of data for the bar chart.

### BarChartSeries

- **name**: `string` - The name of the series.
- **elements**: `BarChartElement[]` - Elements within the series.

### BarChartElement

- **label**: `string` - Label for the data point.
- **value**: `number` - Value for the corresponding label.

### Line Chart (CrowdfundingProjectLineChart)

Represents a line chart used in the project.

- **type**: `"line_chart"` - The type is fixed as `"line_chart"`.
- **series**: `LineChartSeries[]` - Series of data for the line chart.

### LineChartSeries

- **name**: `string` - The name of the series.
- **elements**: `LineChartElement[]` - Elements within the series.

### LineChartElement

- **label**: `string` - Label for the data point.
- **value**: `number` - Value for the corresponding label.

---

## CrowdfundingProjectTimeline

Represents the timeline of a project, including key events.

- **id**: `string` - Unique identifier of the timeline.
- **timeline**: `TimelineEvent[]` - A list of events in the project’s timeline.

### TimelineEvent

Represents an individual event in a project's timeline.

- **date**: `Date` - The date of the event.
- **title**: `string` - The title of the event.
- **description**: `string` - A brief description of the event.

---

## ProjectReward

Represents a reward offered to backers of a project.

- **name**: `string` - The name of the reward.
- **imageUrl**: `string` - URL of the reward image.
- **description**: `string` - Description of the reward.