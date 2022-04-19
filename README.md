[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/idle)
[![Stars on Vaadin Directory](https://img.shields.io/vaadin-directory/star/idle.svg)](https://vaadin.com/directory/component/idle)

# Idle - User inactivity tracking for Vaadin

With *Idle* you can monitor, if user is active or inactive in your application. 

This is done by tracking keyboard and mouse events in window. 
You can specify an inactivity time after which the BODY element is applied 
`userinactive` style. Also a optionally event to server-side component is sent.

## Online demo

Try the add-on demo at http://sami.app.fi/idle

In the demo you can see CSS color changes applied immediately and text chenged on Listener.

## Release notes

### Version 1.0
- Apply CSS changes based on user inactivity
- Server-side listener for idle/resume


## Issue tracking

The issues for this add-on are tracked on [github.com issues page](https://github.com/samie/Idle/issues). All bug reports and feature 
requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Send a pull request for the original project

## License

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.
