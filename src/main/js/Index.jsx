import  
  React, { 
  StrictMode 
} from "react";

import { 
  createRoot 
} from "react-dom/client";

import { 
  createBrowserRouter,
  RouterProvider,
  Link,
  Outlet
} from "react-router-dom";

import { createTheme, MantineProvider } from '@mantine/core';

const theme = createTheme({})

// core styles are required for all packages
import '@mantine/core/styles.css';

// // other css files are required only if
// // you are using components from the corresponding package
// // import '@mantine/dates/styles.css';
// // import '@mantine/dropzone/styles.css';
// // import '@mantine/code-highlight/styles.css';
// // ...

import ErrorPage                    from "/src/main/js/pages/error/ErrorPage.jsx";
import HomePage                     from "/src/main/js/pages/home/HomePage.jsx";
import SplashPage                   from "/src/main/js/pages/splash/SplashPage.jsx";
import RegionsPage                  from "/src/main/js/pages/regions/RegionsPage.tsx";
import EventsPage                   from "/src/main/js/pages/events/EventsPage.jsx";
import Root                         from "/src/main/js/components/root/Root.jsx";

async function rootLoader({params}) {

  const userInfoResponse = await fetch("/api/userinfo");

  var userInfo = {};

  if ( userInfoResponse.ok) {
    userInfo = await userInfoResponse.json()
  }  else {
    userInfo.username = "Guest"
    userInfo.authenticated = false;
  }

  console.log( " userInfo is : " + JSON.stringify(userInfo) )
  return { userInfo }

}

const router = createBrowserRouter([
  // this is the common root for logged in users ( has navbar links )
  {
    path: "/user",
    element: <Root/>,
    loader: rootLoader,
    errorElement: <ErrorPage/>,
    children: [
      {
        path: "home",
        element: <HomePage/>
      },
      {
        path: "events",
        element: <EventsPage/>
      },
      {
        path: "regions",
        element: <RegionsPage/>
      },
      {
        path: "compilers",
        element: <></>
      },
    ]
  },
  // this is intended for anyone (thus no navbar)
  {
    path: "/",
    element: <SplashPage/>,
    loader: rootLoader, /* note use of rootLoader, maybe this name should change */
    errorElement: <ErrorPage/>,
  }
]);

const root = createRoot(document.getElementById("root"));
root.render(
  <StrictMode>
    <MantineProvider theme={theme}>
      <RouterProvider router={router} />
    </MantineProvider>
  </StrictMode>
);
