/* using Header with simple header as base https://ui.mantine.dev/category/headers/  */

import React, { useState } from 'react';
import { useLoaderData } from 'react-router-dom';

import { Button } from '@mantine/core';

import classes from './Header.module.css';

//const { userInfo } = useLoaderData();
const userInfo = {}
userInfo.authenticated = false;
userInfo.username = "Guest 2";

export default function Header() {
  return (
    <header className={classes.header}>
      <div className={classes.containerFlex}>
        <div></div>
        <div></div>
        <div className={classes.buttonFlex}>
          {userInfo.authenticated
            ? <> 
                <Button variant="outline" color="green" onClick={() => {window.location.href="/login"}  }>Login</Button>
                <Button variant="filled"  color="green" onClick={() => {window.location.href="/signup"} }>Sign Up</Button>
              </>
            : <>
                <div>{userInfo.username}</div>
                <Button variant="filled" color="red" onClick={() => {window.location.href="/logout"}  }>Logout</Button>
              </>
          }
        </div>
      </div>
    </header>
  );
}

