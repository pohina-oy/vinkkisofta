
exports.up = function (knex, Promise) {
  return knex.schema.alterTable('bookmarks', t => {
    t.string('creatorId')
  })
}

exports.down = function (knex, Promise) {
  return knex.schema.hasTable('bookmarks').then(exists => {
    if (exists) {
      return knex.schema.alterTable('bookmarks', t => {
        t.dropColumn('creatorId')
      })
    }
  })
}
