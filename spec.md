# Request

- header
    + API-key
    + API-sign
- GET
- POST
    + nonce
    + opt - can we get this straight from google two factor auth?
    
# Response

- error
    + \<char-severity code>\<string-error category>:\<string-error type>\[:\<string-extra info>]
    + severity code
        - E - error
        - W - warning
- result
    + may be missing if error is present
    + JSON format
    