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
import RegionsPage                  from "/src/main/js/pages/regions/RegionsPage.jsx";
import EventsPage                   from "/src/main/js/pages/events/EventsPage.jsx";
import Root                         from "/src/main/js/components/root/Root.jsx";

async function rootLoader({params}) {

  /*
  const userInfoResponse = await fetch("/api/user/info");
  var userInfo = await userInfoResponse.json()

  if ( userInfoResponse.status != 200 || userInfo == null) {
    console.log("there was an error getting user info")
    userInfo = { id:-1, username:"error", isGuest:true }
    return { userInfo }
  }

  return { userInfo };

  */

  var userInfo = {};
  userInfo.username = "Guest"
  userInfo.authenticated = false;

  return { userInfo }

}

const router = createBrowserRouter([
  {
    path: "/",
    element: <Root/>,
    loader: rootLoader,
    errorElement: <ErrorPage/>,
    children: [
      {
        path: "/home",
        element: <></>
      },
      {
        path: "/events",
        element: <EventsPage/>
      },
      {
        path: "/regions",
        element: <RegionsPage/>
      },
      {
        path: "/compilers",
        element: <></>
      },
    ]
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
