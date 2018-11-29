
exports.up = function(knex, Promise) {
  return knex.schema.hasTable('tags').then(exists => {
    if (!exists) {
      return knex.schema.createTable('tags', t => {
        t.string('id').primary()
        t.string('name')
      })
    }
  })
}

exports.down = function(knex, Promise) {
  return knex.schema.dropTableIfExists('tags')
}
