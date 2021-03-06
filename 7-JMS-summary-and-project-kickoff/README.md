## Labs 7 - JMS summary/project kickoff##

### Requirements ###
- Run Linux on UAM workstation
- Use IntelliJ IDEA

### Goals ###
- JMS summary
- JMS project planning
- Q&A

#### JMS summary ####
- Messaging models
- Spring JMS
- QA

#### JMS project planning ####
System contains 4 separated components
- Warehouse: single instance
- Point Of Sale (POS): multiple instances
- Reporting: single instance
- "supermarket/prototype" module is:
	- Prototype implementation 
	- Without JMS 
	- All business requirements are implemented

Requirements:
- Divide components from prototype into separated java modules and connect them via JMS
- Warehouse 
	- Contains product list
	- User can change product price only in Warehouse 
	- Is sending "price change" messages
	- "price change" message is triggered by user manually from Warehouse command line UI
	- Is sending "full product list" messages
	- "full product list" message is triggered by user manually from Warehouse command line UI

- Point Of Sale
	- Contains product list
	- Is receiving "price change" messages
	- Is receiving "full product list" messages
	- Is sending "sales information" messages to Reporting system
	- Sales is possible only if full product list is available locally

- Reporting
	- Counts all sales facts
	- Is receiving "sales information" messages

- General requirements
	- Components will communicate with others only via JMS (Queue/Topic)
	- Spring JMS is required
	- POJO and message converters are nice to have. Common module can contains shared code
    - "mvn clean install" on top level is required to get changes from common module
    - Command line UI is ok

Use Cases
- Adding new Point Of Sale scenario (minimum requirement for 4.0):
	- Start new Point Of Sale
	- Go to Warehouse command line UI
	- Send "full product list" message to all Point Of Sales (new Point Of Sale is include)

- Changing price (minimum requirement for 5.0):
	- Go to Warehouse command line UI
	- Select product and set new price for it
	- "price change" message should be published automatically

- Selling on Point Of Sale (minimum requirement for 4.0):
	- Go to Point Of Sale command line UI
	- Select product to sale (by giving name) and press enter
	- "sales information" message should be published automatically

- Check reports (minimum requirement for 4.0)
	- Go to Reporting command line UI
	- Check UI, report are printed per "sales information" message

Project plan:
- All clarifications need to be done until 27.11.2014
- Implementation need to be done until 04.12.2014
- Send Source code to mateusz.jancy@gmail.com (mail should contain link to github/bitbucket repository or zip file with source code)

##[Feedback](http://goo.gl/forms/DmWOfcJnRV)##
