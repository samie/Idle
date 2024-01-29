[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/idle)
[![Stars on Vaadin Directory](https://img.shields.io/vaadin-directory/star/idle.svg)](https://vaadin.com/directory/component/idle)

# Idle - User inactivity tracking for Vaadin

With *Idle*, you can monitor whether a user is active or inactive in your application. 

This is achieved by tracking keyboard and mouse events within the window. You can set a specified inactivity time, after which the `userinactive` style is applied to the `BODY` element. Additionally, an optional event can be sent to the server-side component.

## Online demo

Try the add-on demo at [idle-demo.fly.dev](https://idle-demo.fly.dev)

In the demo you can see CSS color changes applied immediately and text changed on server-side listeners.

## Release notes

### Version 1.0
- Apply CSS changes based on user inactivity
- Server-side listener for idle/resume

### Version 2.0
- Add support for Java 8 lambdas and Vaadin 8

### Version 3.0
- Add support for Vaadin 24


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
