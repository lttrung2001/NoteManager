const express = require('express')
const app = express()
const jwt = require('jsonwebtoken')
const sql = require('mssql')
app.use(express.json())
const sqlConfig = {
  user: 'sa',
  password: 'tt',
  database: 'API',
  server: 'DESKTOP-4UNL892\\SERVER0',
  port: 1434,
  pool: {
    max: 10,
    min: 1,
    idleTimeoutMillis: 30000
  },
  options: {
    trustServerCertificate: true // change to true for local dev / self-signed certs
  }
}

  sql.connect(sqlConfig,(err) => {
  if (err) {
    console.log('Error when connecting SQL SERVER')
    console.log(err.message)
  } else {
    console.log('Connect successfully')
    app.post('/register', (req, res) => {
      const newUser = {
          name: req.body.name,
          email: req.body.email,
          password: req.body.password
      }
      sql.query(`select * from USERS where email = '${newUser.email}'`, (err, result) => {
        if (err) {
          console.log('Error while query database')
          res.status(400).send()
        } else {
          if (!result.recordset) {
            request.query(`insert into USERS values('${newUser.name}', '${newUser.email}', '${newUser.password}')`)
            res.status(200).json({code: 200, message: null, data: token})
          } else {
            res.status(400).send()
          }
        }
      })
  })
  app.post('/login', (req, res) => {
    const user = {
      email: req.body.email,
      password: req.body.password
    }
    console.log(`Cảnh báo có người truy cập =)))`)
    console.log(user)
    sql.query(`select * from USERS where email = '${user.email}' and password = '${user.password}'`, (err, result) => {
      if (err) {
        console.log(err.message)
        res.status(400).send()
      } else {
        if (result.recordset) {
          const token = jwt.sign({user}, 'API', {expiresIn: "1h"})
          res.status(200).json({code: 200, message: null, data: token})
        } else {
            res.status(404).send()
        }
      }
    })
})
  app.get('/get-all-user', (req, res) => {
    console.log(req.headers)
    if (req.header('Authorization')) {
      const request = new sql.Request()
      request.query(`select * from USERS`, (err, result) => {
        if (err) {
          console.log(err.message)
          res.status(404).send()
        } else {
          console.log(result.recordset)
          res.status(200).json({code: 200, message: null, token: result.recordset})
        }
      })
    }
  })
  }
})
app.listen(3000,() => {
  console.log('Listening on port 3000...')
})