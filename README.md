# Android Call Log Web-Server
This App provides call log data to clients on the same network.

## How to use the App

Once the App is started you see a (very basic) UI that shows your current IP address and the Port
the server is listening for requests. You also see a scrollable list of the user call log history.

You can then make GET requests for the following api's:

* `/` : gives you a list of running services
* `/status` : shows you if there is an ongoing call
* `/log` : a call log history

All responses return a json format.

## Code Structure

There is the [MainActivity](./app/src/main/java/com/bunk/ui/MainActivity.kt) that hosts the basic UI
along with the [MainViewModel](./app/src/main/java/com/bunk/ui/MainViewModel.kt) and
[MainScreen](./app/src/main/java/com/bunk/ui/MainScreen.kt) (Compose). The `MainViewModel` uses two
UseCase(s) to get access to the data. The UseCase are there to hide the information where those data
is coming from.

Within the [App](./app/src/main/java/com/bunk/App.kt)(lication) class the
[Server](./app/src/main/java/com/bunk/server/Server.kt) (an instance of
[KtorServer](./app/src/main/java/com/bunk/server/KtorServer.kt))
gets started. So as long as the `App` is running the server should run as well. Because the `Server`
is an interface, a concrete implementation could be exchanged in future if another type of server is
required (better performance or less memory).

To keep the details of the API service `/`, `/log` and `/status` away from the concrete server
implementation I created a [Service](./app/src/main/java/com/bunk/server/service/Service.kt)
interface. Every concrete service can be implemented by implementing this interface.
See `StatusService`,
`RootService` and `LogService`. The concrete `KtorServer` can get an instance from `ServiceFactory`
of each `Service`. This decoupling will help exchanging concrete Server implementation in the future
and not to having duplicated source of truth.

I implemented a very basic permission handling.
The [AppPermission](./app/src/main/java/com/bunk/permission/AppPermission.kt)
declares all permissions the app needs.
The [PermissionHelper](./app/src/main/java/com/bunk/permission/PermissionHelper.kt)
can be used to verify if an `AppPermission` has been granted. I know permissions should be asked as
late as possible, but for this application it makes sense asking all permissions at App start. If a
single required `AppPermission` has not been granted the App will close itself and shows a Toast
that the user should grant all permissions via Settings -> App -> Permissions. In a real work
situation I would implement a more sophisticated permission handling using
`shouldShowRationaly()` and showing proper dialog to the user.

To let the App know about ongoing calls I implemented a
[CallBroadcastReceiver](./app/src/main/java/com/bunk/call/CallBroadcastReceiver.kt) that is
listening for `android.intent.action.PHONE_STATE` action. When a call is detected it will read the
required data and store them
into [OngoingCallStorage](./app/src/main/java/com/bunk/call/storage/OngoingCallStorage.kt)
with an in-memory implementation of that interface.

The call log is read
from [CallLogStorage](./app/src/main/java/com/bunk/call/storage/CallLogStorage.kt). This interface
has a `ContentResolver` implementation. Because it is not needed everytime a get request to `/log`
is made to query all contacts again (as most likely no new calls happened since last request) I
cache the results from the query. Everytime a new call has been detected
from `CallBroadcastReceiver` it marks the data as stale on the `CallLogStorage`, so the next request
to `/log` will make a new query to the `ContentResolver`.

[NumberOfCallStorage](./app/src/main/java/com/bunk/call/storage/NumberOfCallStorage.kt) stores the
number of queries for an ongoing call with every request to `/status` for the current phone number.
This is implemented with `SharedPreferences`.

I covered most classes with unit tests and did some basic Espresso Tests checking if the Compose
views are displayed.

## Known Issues

* After reboot the app must be started manually to make the CallReceiver work
* There was no specification what to show when there is no ongoing call. So when there is no ongoing
  call `number` and `name` are null.
* The time format is slightly different. I use `2022-03-16T15:49:06+0100`
  instead `"2022-03-16T15:49:06+01:00`. In a real work situation I would double check this with the
  Stakeholder and invest more time fixing this.
* Everytime there is an ongoing call and you make a request to `/status` every callLog entry with
  this number increments the `timesQueried`. As this was not closer specified in the task I consider
  this as okay. In real world I would double check this behavior with the stakeholder.

## Notes

* The server is implemented with [Ktor](https://ktor.io/)
* The serialization to JSON I am
  using [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
* I used the Android Studio Kotlin Style guide for code formatting.
* I use [refreshVersions](https://jmfayard.github.io/refreshVersions/) to keep a clean overview on
  library versions
* I use JUnit4 for unit testing
* I used [AssertJ](https://assertj.github.io/doc/) as a fluent assertion Api
* For mocking I use [Mockk](https://mockk.io/)
* The UI is implemented with [Jetpack Compose](https://developer.android.com/jetpack/compose)

## What I did not

* Extensively creating Repositories and UseCases and call it Clean Code. Clean code is more than
  those classes. Its about separating components, having clear responsibilities and as less
  complexity as possible. Every additional layer is increasing complexity and so should only be
  added to solve real problems. This application is so small that I only created storage classes and
  one `GetCallStatusUseCase`. However real / bigger application require additional layers and
  boundaries and then I would create UseCases, Repositories and other architectural boundaries if
  needed.
* Making a beautiful UserInterface. In real work situation I would make it (together with designers)
  as most user friendly as I can.
* Testing the serialization to Json. I trust the `kotlinx-serilization`.
* Separating the code into gradle modules. Why? Because multi gradle modules comes with cost of
  build performance. This is critical when you have very tiny application and only a few classes. In
  bigger Apps having many more classes I would consider creating modules. For this application a
  dedicated `server` module could make sense. Maybe some `data` module also makes sense, as they are
  mostly stable and do not change often. Another module could hold most of the business logic
  like `domain` or `core`. I would also consider multi modules to when having cross-functional
  teams, so every team could work on their dedicated module. I also did not create multi module
  because it increases the complexity of dependency (injection) configuration.
* Any server security related topics. In real work situation I would ensure making a webserver safe
  and secure.