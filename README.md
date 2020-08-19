# SimpleDriverAssistant

<img src="/presentation/demo.gif" width="300" align="right" hspace="0" />

**Praca Inżynierska / Engineering Thesis**

**Projekt i implementacja mobilnej aplikacji do informowania o zdarzeniach drogowych / The Project and implementation of a mobile application for reporting road accidents**

# Documentation

**[Application Presentation](https://photos.app.goo.gl/r1gfx6jx3M2Fcwsy5)**

**[Dokumentacja projektu PL](https://github.com/pavvel42/SimpleDriverAssistant/blob/master/Projekt-i-implementacja-mobilnej-aplikacji-do-informowania-o-zdarzeniach-drogowych.pdf)**

# Used libraries

**[Picasso](https://github.com/square/picasso)**

**[CircleImageView](https://github.com/hdodenhof/CircleImageView)**

**[FloatingActionButtonSpeedDial](https://github.com/leinardi/FloatingActionButtonSpeedDial)**

**[EasyPermissions](https://github.com/googlesamples/easypermissions)**

**[Gson](https://github.com/google/gson)**

_min API Version 23_

Layout sketch ready to use. Requires to be connected to your Firebase project.

# How to run

1. Clone this repository.
2. Get **google-services.json** from your Firebase project.
    1. Follow only the first **three steps** in this [documentation](https://firebase.google.com/docs/android/setup?authuser=0) (the last step is **3.1 b**).
3. Enable Google Sign-In in the Firebase console:
    1. In the Firebase console, open the **Auth** section.
    2. On the **Sign in method** tab, enable the **Google** sign-in method and click **Save**.
4. Once you have placed the **google-services.json** in a cloned project, in the IDE do **rebuild project**.
5. Run ‘app’.

# How to add function to firebase project

**[Deploy this functions](https://github.com/pavvel42/SimpleDriverAssistant/tree/master/Firebase%20Function)** **[according to this manual](https://youtu.be/DYfP-UIKxH0?list=PLl-K7zZEsYLkPZHe41m4jfAxUi0JjLgSM)**
**[Don't forget about Add the Firebase Admin SDK](https://github.com/googlesamples/easypermissions)**