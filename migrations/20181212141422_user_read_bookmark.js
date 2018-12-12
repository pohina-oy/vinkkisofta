
exports.up = function(knex, Promise) {
  return knex.schema.hasTable('user_read_bookmarks').then(exists => {
    if (!exists) {
      return knex.schema.createTable('user_read_bookmarks', t => {
        t.string('userId').references('users.id')
        t.string('bookmarkId').references('bookmarks.id')
        t.timestamp('readDate')
      })
    }
  })
}

exports.down = function(knex, Promise) {
  return knex.schema.dropTableIfExists('user_read_bookmarks')
}
