class AddDefaultResponseColumn< ActiveRecord::Migration
  def self.up
    add_column( :conversations, :default_response, :string, :limit=>1000)
  end

  def self.down
    remove_column(:conversations, :default_response)
  end
end
