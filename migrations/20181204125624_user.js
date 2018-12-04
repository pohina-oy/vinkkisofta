
exports.up = function(knex, Promise) {
  return knex.schema.hasTable('users').then(exists => {
    if (!exists) {
      return knex.schema.createTable('users', t => {
        t.string('id').primary()
        t.string('email')
        t.string('username')
        t.integer('githubId')
      })
    }
  })
  
}

exports.down = function(knex, Promise) {
  return knex.schema.dropTableIfExists('users')
}
