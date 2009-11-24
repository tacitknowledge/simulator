class Configuration < ActiveRecord::Base
# Remove associations to prevent conflicts
#  belongs_to :transport
#  belongs_to :format

  validates_presence_of :attribute_name, :attribute_value
end
