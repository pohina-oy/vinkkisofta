require('dotenv').config()
const pg = require('pg')

const client = new pg.Client({
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD
})

client.connect()

client.query('CREATE DATABASE vinkkisofta')
  .then(() => {
      console.log('database created')
      process.exit(0)
    })
  .catch(e => console.log(e))