class Conversation < ActiveRecord::Base
  has_one :scenario
  belongs_to :system
end
