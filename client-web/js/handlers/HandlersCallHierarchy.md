# Call Hierarchy Documentation

Here's a document to better show how the hierarchy between functions works.  
To keep modularity and to be able to re-use pieces of code, a layered structure has been chosen.
Here's the call hierarchy for each handler. 

---

## LOGIN

**Call Sequence:**
- [login](../login.js)
  - ➝ [handleLogin](./handleLogin.js)
    - ➝ [handleServerRequest](./handleServerRequest.js)
    - ➝ `callback` *(specified by the parent caller)*

---

## REGISTER

**Call Sequence:**
- [register](../register.js)
  - ➝ [handleServerRequest](./handleServerRequest.js)
  - ➝ `callback` *(specified by the parent caller)*

**If response code is `200` (user successfully registered):**
- ➝ [handleLogin](./handleLogin.js)
  - ➝ [handleServerRequest](./handleServerRequest.js)
  - ➝ `callback` *(specified by the parent caller)*

---

## BALANCE

**Call Sequence:**
- [balance](../balance.js)
  - ➝ [handleBalance](./handleBalance.js)
    - ➝ [handleServerRequest](./handleServerRequest.js)
    - ➝ `callback` *(specified by the parent caller)*

> `handleBalance` can be called with default callbacks to update the `sessionStorage` with newer values.

---

## MODIFY

**Call Sequence:**
- [modify](../modify.js)
  - ➝ [handleServerRequest](./handleServerRequest.js)
  - ➝ `callback` *(specified by the parent caller)*

---

## STATS

**Call Sequence:**
- [stats](../stats.js)
  - ➝ [handleServerRequest](./handleServerRequest.js)
  - ➝ `callback` *(specified by the parent caller)*

---

## HISTORY

**Call Sequence:**
- [history](../history.js)
  - ➝ [handleServerRequest](./handleServerRequest.js)
  - ➝ `callback` *(specified by the parent caller)*