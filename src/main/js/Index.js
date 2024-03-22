import React, { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";

import ErrorPage                                  from "/src/main/js/error/Error.jsx";
import Root                                       from "/src/main/js/Root.jsx";

async function rootLoader({params}) {

  /*
  const userInfoResponse = await fetch("/api/user/info");
  var userInfo = await userInfoResponse.json()

  if ( userInfoResponse.status != 200 || userInfo == null) {
    console.log("there was an error getting user info")
    userInfo = { id:-1, username:"error", isGuest:true }
    return { userInfo }
  }
  */

  //return { userInfo };

}

const router = createBrowserRouter([
  {
    path: "/",
    element: <Root />,
    loader: rootLoader,
    id:"root",
    errorElement: <ErrorPage />,
    children: []
  },
]);

const root = createRoot(document.getElementById("root"));

root.render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>
);
