/* using Header with simple header as base https://ui.mantine.dev/category/headers/  */

import React, { useState } from 'react';
import { useLoaderData } from 'react-router-dom';

import { Button } from '@mantine/core';

import classes from './Header.module.css';

export default function Header() {

  const { userInfo } = useLoaderData();

  return (
    <header className={classes.header}>
      <div className={classes.containerFlex}>
        <div></div>
        <div></div>
        <div className={classes.buttonFlex}>
          {userInfo.authenticated
            ?            
              <>
                <span className={classes.usernameSpan}>{userInfo.username}</span>
                <Button variant="filled" color="red" onClick={() => {window.location.href="/logout"}  }>Logout</Button>
              </>
            :
              <> 
                <Button variant="outline" color="green" onClick={() => {window.location.href="/login"}  }>Login</Button>
                <Button variant="filled"  color="green" onClick={() => {window.location.href="/signup"} }>Sign Up</Button>
              </>
          }
        </div>
      </div>
    </header>
  );
}

